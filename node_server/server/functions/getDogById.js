var dog = require('../models/dog');

exports.getDogProfile = dogId =>

	new Promise(function(resolve,reject){

		dog.find({ dogId: dogId })

		.then(function(dogs){
			resolve(dogs[0]);
		})
		.catch(function(err){
			reject({ status: 500, message: 'Internal Server Error !' });
		});

	});
