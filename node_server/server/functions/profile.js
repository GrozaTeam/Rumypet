var user = require('../models/user');

exports.getProfile = email =>

	new Promise(function(resolve,reject){

		user.find({ email: email }, { name: 1, email: 1, phone:1, created_at: 1, _id: 0 })
		.then(function(users){
			resolve(users[0]);
		})
		.catch(function(err){
			console.log('err:', err);
			reject({ status: 500, message: 'Internal Server Error !' });
		});
	});
