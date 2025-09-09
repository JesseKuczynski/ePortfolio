from pymongo import MongoClient

class AnimalShelter(object):
    """ CRUD operations for Animal collection in MongoDB """

    def __init__(self):
        # Connection Variables
        USER = 'aacuser'
        PASS = 'Password'  
        HOST = 'nv-desktop-services.apporto.com'
        PORT = 33032
        DB = 'AAC'  
        COL = 'animals'

        # Initialize Connection
        self.client = MongoClient(f'mongodb://{USER}:{PASS}@{HOST}:{PORT}')
        self.database = self.client[DB]
        self.collection = self.database[COL]
        
    # C in CRUD
    def create(self, data):
        if data is not None:
            if not all(isinstance(key, str) for key in data.keys()):
                return "Error: documents must have only string keys"
            
            try:
                result = self.collection.insert_one(data)
                if result.inserted_id:
                    return True
            except Exception as e:
                return f"Error: {e}"
        else:
            raise Exception("Nothing to save, because data parameter is empty")
    
    # R in CRUD
    def read(self, criteria=None):
        if criteria is not None:
            try:
                data = self.collection.find(criteria, {"_id": False})
                return list(data)
            except Exception as e:
                raise Exception(f"Error reading from database: {e}")
        else:
            try:
                data = self.collection.find({}, {"_id": False})
                return list(data)
            except Exception as e:
                raise Exception(f"Error reading from database: {e}")
    # U in CRUD
    def update(self, searchData, updateData):
        if searchData is not None:
            result = self.database.animals.update_many(searchData, {"$set": updateData })
        else:
            return "{}"
        return result.raw_result
    
    # D in CRUD
    def delete(self, deleteData):
        if deleteData is not None:
            result = self.database.animals.delete_many(deleteData)
        else:
            return "{}"
        return result.raw_result