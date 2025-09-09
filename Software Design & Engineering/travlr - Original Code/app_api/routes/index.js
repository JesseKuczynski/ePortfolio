const express = require("express");
const router = express.Router();

// This is where we import the controllers we will route
const tripsController = require("../controllers/trips");
const authController = require("../controllers/authentication");

const jwt = require('jsonwebtoken'); // Enable JSON Web Tokens

// Method to authenticate our JWT
function authenticateJWT(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (authHeader == null) {
    console.log('Auth Header Required but NOT PRESENT!');
    return res.sendStatus(401);
  }

  const headers = authHeader.split(' ');
  if (headers.length < 2) {
    console.log('Not enough tokens in Auth Header: ' + headers.length);
    return res.sendStatus(401);
  }

  const token = headers[1];
  if (token == null) {
    console.log('Null Bearer Token');
    return res.sendStatus(401);
  }

  jwt.verify(token, process.env.JWT_SECRET, (err, verified) => {
    if (err) {
      console.log('Token Validation Error!');
      return res.status(401).json({ message: 'Token Validation Error!' });
    }

    req.auth = verified; // Attach the decoded token
    next(); // Move forward
  });
}


router.route("/register").post(authController.register);
router.route("/login").post(authController.login);

// define route for our trips endpoint
router
    .route('/trips')
    .get(tripsController.tripsList) // GET Method routes tripList
    .post(authenticateJWT, tripsController.tripsAddTrip); // POST method Adds a Trip

// GET Method routes tripsFindByCode - requires parameter
router
    .route('/trips/:tripCode')
    .get(tripsController.tripsFindByCode)    
    .put(authenticateJWT, tripsController.tripsUpdateTrip);

// define route for login endpoint
router
    .route('/login')
    .post(authController.login);

module.exports = router;