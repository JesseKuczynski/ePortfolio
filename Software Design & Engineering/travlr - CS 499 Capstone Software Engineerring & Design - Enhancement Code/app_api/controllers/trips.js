const mongoose = require('mongoose');
const Trip = require('../models/travlr'); 
const Model = mongoose.model('trips');
const Booking = require('../models/booking');


const tripsList = async(req, res) => {
    const q = await Model
        .find({})
        .exec();

    if(!q)
    {
        return res
                .status(404)
                .json(err);
    } else {
        return res
            .status(200)
            .json(q);
    }    
};

const tripsFindByCode = async(req, res) => {
    const q = await Model
        .find({'code' : req.params.tripCode}) 
        .exec();



    if(!q)
    { 
        return res
                .status(404)
                .json(err);
    } else { 
        return res
            .status(200)
            .json(q);
    }

};


const tripsAddTrip = async(req, res) => {
    const newTrip = new Trip({
        code: req.body.code,
        name: req.body.name,
        length: req.body.length,
        start: req.body.start,
        resort: req.body.resort,
        perPerson: req.body.perPerson,
        image: req.body.image,
        description: req.body.description
    });

    const q = await newTrip.save();

        if(!q)
        { 
            return res
                .status(400)
                .json(err);
        }   else { 
            return res
                .status(201)
                .json(q);
        }
};


const tripsUpdateTrip = async(req, res) => {


    console.log(req.params);
    console.log(req.body);

    const q = await Model
        .findOneAndUpdate(
            { 'code' : req.params.tripCode },
            {
                code: req.body.code,
                name: req.body.name,
                length: req.body.length,
                start: req.body.start,
                resort: req.body.resort,
                perPerson: req.body.perPerson,
                image: req.body.image,
                description: req.body.description
            }
        )
        .exec();

        if(!q)
        { 
            return res
                .status(200)
                .json(err);
        } else { 
            return res
                .status(400)
                .json(q);
        }


};

/**
 * POST /api/trips/:tripCode/bookings
 * Creates booking document
 * Fields: fullName, email, travelers, start
 */

const tripsCreateBooking = async (req, res) => {
  try {
    const trip = await Model.findOne({ code: req.params.tripCode }).exec();
    if (!trip) return res.status(404).json({ message: 'Trip not found' });

    const booking = await Booking.create({
      tripCode: trip.code,
      trip: trip._id,
      fullName: req.body.fullName,
      email: req.body.email,
      travelers: req.body.travelers,
      start: req.body.start  
    });

    return res.status(201).json(booking);
  } catch (err) {
    return res.status(400).json({ message: err.message || 'Booking failed' });
  }
};

module.exports = {
    tripsList,
    tripsFindByCode,
    tripsAddTrip,
    tripsUpdateTrip,
    tripsCreateBooking
};