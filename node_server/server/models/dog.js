'use strict';

const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const dogSchema = mongoose.Schema({

  dogId : {type: String, unique: true},
  ownerId : String,
	dogName 			: String,
	dogGender : String,
	dogSpecies : String,
  dogBirth : String,
	created_at		: String

});

mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost:27017/node-login');

module.exports = mongoose.model('dog', dogSchema);
