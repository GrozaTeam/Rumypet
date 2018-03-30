import mongoose from 'mongoose';

/*
 * Define the schema model for user.
 */
const Schema = mongoose.Schema;

const userSchema = new Schema({
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


export default mongoose.model('User', userSchema);
