const mongoose = require('mongoose');
const userSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'Name is required'],
    trim: true,
  },
  email: {
    type: String,
    required: [true, 'Email is required'],
  },
  age: {
    type: Number,
  }
}, {
  timestamps: true, 
});
userSchema.index({ email: 1 });
const User = mongoose.model('User', userSchema);
module.exports = User;
