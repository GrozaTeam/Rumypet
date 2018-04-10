'use strict';

const mongoose = require('mongoose');

const Schema = mongoose.Schema;

const userSchema = mongoose.Schema({

	name 			: String,
	email			: {type: String, unique: true},
	phone : String,
	hashed_password	: String,
	created_at		: String,
	temp_password	: String,
	temp_password_time: String

});

module.exports = mongoose.model('user', userSchema);
