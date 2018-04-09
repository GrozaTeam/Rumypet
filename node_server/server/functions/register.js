'use strict';

const user = require('../models/user');
const bcrypt = require('bcryptjs');

exports.registerUser = (name, email, password) =>

	new Promise((resolve,reject) => {

	  var salt = bcrypt.genSaltSync(10);
		var hash = bcrypt.hashSync(password, salt);

		var newUser = new user({

			name: name,
			email: email,
			hashed_password: hash,
      phone: phone,
			created_at: new Date()
		});

		newUser.save()

		.then(() => resolve({ status: 201, message: 'User Registered Sucessfully !' }))

		.catch(err => {

			if (err.code == 11000) {

				reject({ status: 409, message: 'User Already Registered !' });

			} else {

				reject({ status: 500, message: 'Internal Server Error !' });
			}
		});
	});
