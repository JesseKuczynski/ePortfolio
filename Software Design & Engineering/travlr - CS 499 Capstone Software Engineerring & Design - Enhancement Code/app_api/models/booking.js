const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema({
  tripCode: { type: String, required: true, index: true },
  trip: { type: mongoose.Schema.Types.ObjectId, ref: 'trips' }, // optional link
  fullName: { type: String, required: true, maxlength: 120 },
  email: { type: String, required: true, match: /.+@.+\..+/ },
  travelers: { type: Number, required: true, min: 1 },
  start: { type: Date, required: true },
  createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('bookings', bookingSchema);
