'use strict';

const dog = require('../models/dog');

exports.registerDog = (dogId, ownerId, dogName, dogGender, dogSpecies, dogBirth) =>

	new Promise((resolve,reject) => {

		const newDog = new dog({
      dogId: dogId,
      ownerId: ownerId,
			dogName: dogName,
			dogGender: dogGender,
			dogSpecies: dogSpecies,
			dogBirth: dogBirth,
			created_at: new Date()
		});

		newDog.save()

		.then(() => {

			console.log('register complete');

			resolve({ status: 201, message: 'Dog Registered Sucessfully !' });

		})

		.catch(err => {

			if (err.code == 11000) {

				reject({ status: 409, message: 'Dog Already Registered !' });

			} else {

				reject({ status: 500, message: 'Internal Server Error !' });
			}
		});
	});
