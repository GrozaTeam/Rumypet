var dog = require('../models/dog');

exports.getDogProfile = function(email){

	new Promise(function(resolve,reject){

		dog.find({ ownerId: email })

		.then(function(dogs){
			resolve(dogs);
		})
		.catch(function(err){
			reject({ status: 500, message: 'Internal Server Error !' });
		});
	});
};
