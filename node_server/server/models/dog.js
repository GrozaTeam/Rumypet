var mongoose = require('mongoose');

var Schema = mongoose.Schema;

var dogSchema = mongoose.Schema({

  dogId : {type: String, unique: true},
  ownerId : String,
	dogName 			: String,
	dogGender : String,
	dogSpecies : String,
  dogBirth : String,
	created_at		: String,
  dogImg : { data: Buffer, contentType: String }

});

module.exports = mongoose.model('dog', dogSchema);
