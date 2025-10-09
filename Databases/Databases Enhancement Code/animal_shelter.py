from pymongo import MongoClient, UpdateOne, UpdateMany, DeleteOne, DeleteMany
from pymongo.errors import PyMongoError
import os
from typing import Iterable, List, Dict, Any, Optional, Tuple

try:
    from dotenv import load_dotenv
    load_dotenv()
except Exception:
    pass


def _require_env(name: str) -> str:
    v = os.getenv(name)
    if v is None or v == "":
        raise RuntimeError(f"Missing required env var: {name}")
    return v


class AnimalShelter:
    """CRUD + bulk ops for AAC 'animals' collection in MongoDB."""

    def __init__(self):
        # Prefer a full MongoDB URI if provided
        uri = os.getenv("AAC_MONGODB_URI")

        if uri:
            DB = os.getenv("AAC_DB", "AAC")
            COL = os.getenv("AAC_COL", "animals")
            self.client = MongoClient(uri, serverSelectionTimeoutMS=5000)
            self.database = self.client[DB]
        else:
            USER = _require_env("AAC_USER")
            PASS = _require_env("AAC_PASS")
            HOST = _require_env("AAC_HOST")
            PORT = int(_require_env("AAC_PORT"))
            DB   = _require_env("AAC_DB")
            COL  = _require_env("AAC_COL")

            self.client = MongoClient(
                f"mongodb://{USER}:{PASS}@{HOST}:{PORT}",
                serverSelectionTimeoutMS=5000,
            )
            self.database = self.client[DB]

        self.collection = self.database[COL]

        # Optional connection check
        try:
            self.client.admin.command("ping")
        except PyMongoError as e:
            raise RuntimeError(f"MongoDB connection failed: {e}")

    # Create
    def create(self, data: Dict[str, Any]) -> bool:
        if not data:
            raise ValueError("Nothing to save: 'data' is empty")
        if not all(isinstance(k, str) for k in data.keys()):
            raise TypeError("Documents must have only string keys")
        try:
            res = self.collection.insert_one(data)
            return bool(res.inserted_id)
        except PyMongoError as e:
            raise RuntimeError(f"Create failed: {e}")

    def bulk_insert(self, docs: Iterable[Dict[str, Any]], ordered: bool = False) -> Dict[str, Any]:
        docs = list(docs or [])
        if not docs:
            return {"inserted_count": 0}
        if not all(isinstance(d, dict) and all(isinstance(k, str) for k in d.keys()) for d in docs):
            raise TypeError("All docs must be dicts with string keys")
        try:
            res = self.collection.insert_many(docs, ordered=ordered)
            return {"inserted_count": len(res.inserted_ids)}
        except PyMongoError as e:
            raise RuntimeError(f"Bulk insert failed: {e}")

    # Read
    def read(
        self,
        criteria: Optional[Dict[str, Any]] = None,
        *,
        projection: Optional[Dict[str, int]] = None,
        limit: Optional[int] = None,
        skip: int = 0,
        sort: Optional[List[Tuple[str, int]]] = None,
        exclude_id: bool = True,
    ) -> List[Dict[str, Any]]:
        criteria = criteria or {}
        proj = dict(projection or {})
        if exclude_id and "_id" not in (projection or {}):
            proj["_id"] = 0
        try:
            cursor = self.collection.find(criteria, proj)
            if sort:
                cursor = cursor.sort(sort)
            if skip:
                cursor = cursor.skip(skip)
            if limit:
                cursor = cursor.limit(limit)
            return list(cursor)
        except PyMongoError as e:
            raise RuntimeError(f"Read failed: {e}")

    # Update
    def update(
        self,
        searchData: Dict[str, Any],
        updateData: Dict[str, Any],
        *,
        upsert: bool = False,
        many: bool = True,
    ) -> Dict[str, Any]:
        if not searchData or not updateData:
            return {"matched_count": 0, "modified_count": 0, "upserted_count": 0}
        try:
            if many:
                res = self.collection.update_many(searchData, {"$set": updateData}, upsert=upsert)
            else:
                res = self.collection.update_one(searchData, {"$set": updateData}, upsert=upsert)
            return {
                "matched_count": res.matched_count,
                "modified_count": res.modified_count,
                "upserted_count": 1 if res.upserted_id is not None else 0,
            }
        except PyMongoError as e:
            raise RuntimeError(f"Update failed: {e}")

    def bulk_update(
        self,
        update_ops: Iterable[Dict[str, Any]],
        *,
        upsert: bool = False,
        many: bool = True,
    ) -> Dict[str, Any]:
        ops = list(update_ops or [])
        if not ops:
            return {"matched_count": 0, "modified_count": 0, "upserted_count": 0}
        writes = []
        for op in ops:
            f = op.get("filter") or {}
            s = op.get("set") or {}
            if not f or not s:
                continue
            op_upsert = bool(op.get("upsert", upsert))
            writes.append((UpdateMany if many else UpdateOne)(f, {"$set": s}, upsert=op_upsert))
        if not writes:
            return {"matched_count": 0, "modified_count": 0, "upserted_count": 0}
        try:
            res = self.collection.bulk_write(writes, ordered=False)
            return {
                "matched_count": getattr(res, "matched_count", 0),
                "modified_count": res.modified_count,
                "upserted_count": len(res.upserted_ids or {}),
            }
        except PyMongoError as e:
            raise RuntimeError(f"Bulk update failed: {e}")

    #  Delete
    def delete(self, deleteData: Dict[str, Any], *, many: bool = True) -> Dict[str, Any]:
        if not deleteData:
            return {"deleted_count": 0}
        try:
            res = (self.collection.delete_many if many else self.collection.delete_one)(deleteData)
            return {"deleted_count": res.deleted_count}
        except PyMongoError as e:
            raise RuntimeError(f"Delete failed: {e}")

    def bulk_delete(self, delete_filters: Iterable[Dict[str, Any]], *, many: bool = True) -> Dict[str, Any]:
        filters = list(delete_filters or [])
        if not filters:
            return {"deleted_count": 0}
        writes = []
        for f in filters:
            if not f:
                continue
            writes.append(DeleteMany(f) if many else DeleteOne(f))
        if not writes:
            return {"deleted_count": 0}
        try:
            res = self.collection.bulk_write(writes, ordered=False)
            return {"deleted_count": res.deleted_count}
        except PyMongoError as e:
            raise RuntimeError(f"Delete failed: {e}")

    # ---------- Counting / Aggregation ----------
    def count(self, criteria: Optional[Dict[str, Any]] = None) -> int:
        try:
            return self.collection.count_documents(criteria or {})
        except PyMongoError as e:
            raise RuntimeError(f"Count failed: {e}")

    def counts_by_field(
        self,
        field: str,
        limit: int = 10,
        criteria: Optional[Dict[str, Any]] = None,
        *,
        sort_desc: bool = True,
    ) -> List[Dict[str, Any]]:
        if not field or not isinstance(field, str):
            raise ValueError("field must be a non-empty string")
        pipeline = []
        if criteria:
            pipeline.append({"$match": criteria})
        pipeline += [
            {"$group": {"_id": f"${field}", "count": {"$sum": 1}}},
            {"$sort": {"count": -1 if sort_desc else 1, "_id": 1}},
        ]
        if limit and limit > 0:
            pipeline.append({"$limit": int(limit)})
        try:
            return list(self.collection.aggregate(pipeline))
        except PyMongoError as e:
            raise RuntimeError(f"Aggregation failed: {e}")
