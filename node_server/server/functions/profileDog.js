'use strict';

const dog = require('../models/dog');

exports.getDogProfile = email =>

	new Promise((resolve,reject) => {

		dog.find({ ownerId: email })

		.then(dogs => resolve(dogs[0]))

		.catch(err => reject({ status: 500, message: 'Internal Server Error !' }))

	});
