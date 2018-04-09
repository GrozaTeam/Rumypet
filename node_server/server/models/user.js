var mongoose =  require('mongoose');

var Schema = mongoose.Schema;

var userSchema = mongoose.Schema({

  email: { type: String, unique: true },
  hashed_password: { type: String, select: false },
  name: {type : String},
  phone: { type: String },
  created_at		: String,
	temp_password	: String,
	temp_password_time: String
  
});
mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost:27017/node-login');

module.exports = mongoose.model('user',userSchema);
