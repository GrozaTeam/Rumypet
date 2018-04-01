var mongoose =  require('mongoose');

/*
 * Define the schema model for user.
 */
var Schema = mongoose.Schema;

var userSchema = new Schema({
  ID: { type: String, unique: true },
  password: { type: String, select: false },
  verified: { type: String, default: false },
  dogname: { type: String, required: true },
  species: String,
  gender: String,
  Birth: String,
  OwnerName: {type : String},
  email:{type:String, unique:true },
  phone: { type: String },
  // user, admin
  permission: { type: String, default: 'user' },
  status: { type: String, default: 'active' },
  fcmToken: { type: String },
  createdAt: {
    type: Date,
    default: Date.now,
  }
});
module.exports = mongoose.model('User',userSchema);