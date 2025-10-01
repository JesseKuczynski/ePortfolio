const express = require("express");
const router = express.Router();

const tripsController = require("../controllers/trips");
const authController = require("../controllers/authentication");

const jwt = require('jsonwebtoken');


function authenticateJWT(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (!authHeader) return res.sendStatus(401);

  const [scheme, token] = authHeader.split(' ');
  if (!token) return res.sendStatus(401);

  jwt.verify(token, process.env.JWT_SECRET, (err, verified) => {
    if (err) return res.status(401).json({ message: 'Token Validation Error!' });
    req.auth = verified;
    next();
  });
}


router.route("/register").post(authController.register);
router.route("/login").post(authController.login);


router
  .route('/trips')
  .get(tripsController.tripsList)
  .post(authenticateJWT, tripsController.tripsAddTrip);


router
  .route('/trips/:tripCode/bookings')
  .post(tripsController.tripsCreateBooking); 


router
  .route('/trips/:tripCode')
  .get(tripsController.tripsFindByCode)
  .put(authenticateJWT, tripsController.tripsUpdateTrip);

module.exports = router;
