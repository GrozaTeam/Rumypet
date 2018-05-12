var express = require('express');
var router = express.Router();

var auth = require('basic-auth');
var jwt = require('jsonwebtoken');

var register = require('../functions/register');
var login = require('../functions/login');
var profile = require('../functions/profile');
var password = require('../functions/password');
var config = require('../config/config.json');

var registerDog = require('../functions/registerDog');
var profileDog = require('../functions/profileDog');

router.get('/', function(req, res) {
  res.end('Welcome to Rumypet !');
});

router.post('/authenticate', function(req, res) {
  var credentials = auth(req);
  if (!credentials) {
    res.status(400).json({
      message: 'Invalid Request !'
    });
  } else {
    login.loginUser(credentials.name, credentials.pass)
      .then(function(result) {
        var token = jwt.sign(result, config.secret, {
          expiresIn: 1440
        });
        res.status(result.status).json({
          message: result.message,
          token: token
        });
      })
      .catch(function(err) {
        res.status(err.status).json({
          message: err.message
        });
      });
  }
});

router.post('/users', function(req, res) {
  var name = req.body.name;
  var email = req.body.email;
  var password = req.body.password;
  var phone = req.body.phone;
  if (!name || !email || !password || !phone || !name.trim() || !email.trim() || !password.trim() || !phone.trim()) {
    res.status(400).json({
      message: 'Invalid Request !'
    });
  } else {
    register.registerUser(name, email, password, phone)
      .then(function(result) {
        console.log('post result for user: ' + result);
        res.setHeader('Location', '/users/' + email);
        res.status(result.status).json({
          message: result.message
        });
      })
      .catch(function(err) {
        res.status(err.status).json({
          message: err.message
        });
      });
  }
});

router.post('/dogs', function(req, res) {
  var dogId = req.body.dogId;
  var ownerId = req.body.ownerId;
  var dogName = req.body.dogName;
  var dogGender = req.body.dogGender;
  var dogSpecies = req.body.dogSpecies;
  var dogBirth = req.body.dogBirth;

  if (!dogId || !ownerId || !dogName || !dogGender || !dogSpecies || !dogBirth || !dogId.trim() || !ownerId.trim() || !dogName.trim() || !dogGender.trim() || !dogBirth.trim() || !dogSpecies.trim()) {
    res.status(400).json({
      message: 'Invalid Request !'
    });
  } else {
    console.log('id: ' + dogId + '/' + ownerId + '/' + dogName + '/' + dogGender + '/' + dogSpecies + '/' + dogBirth);
    registerDog.registerDog(dogId, ownerId, dogName, dogGender, dogSpecies, dogBirth)
      .then(function(result) {
        console.log('post result: ' + result);
        res.setHeader('Location', '/dogs/' + ownerId);
        res.status(result.status).json({
          message: result.message
        });
      })
      .catch(function(err) {
        res.status(err.status).json({
          message: err.message
        });

      });
  }

});

router.get('/users/:id', function(req, res) {

  if (checkToken(req)) {
    profile.getProfile(req.params.id)
      .then(function(result) {
        console.log('user result : ' + result);
        res.json(result);
      })
      .catch(function(err) {
        console.log('user err : ' + err);
        res.status(err.status).json({
          message: err.message
        });
      });
  } else {
    res.status(401).json({
      message: 'Invalid Token !'
    });
  }
});

router.get('/dogs/:id', function(req, res) {
  if (checkToken(req)) {
    console.log("id == " + req.params.id);
    profileDog.getDogProfile(req.params.id)
      .then(function(result) {
        console.log('dog result : ' + result);
        res.json(result);
      })
      .catch(function(err) {
        console.log('dog err : ' + err);
        res.status(err.status).json({
          message: err.message
        });
      });
  } else {
    res.status(401).json({
      message: 'Invalid Token !'
    });
  }

});

router.put('/users/:id', function(req, res) {

  if (checkToken(req)) {

    var oldPassword = req.body.password;
    var newPassword = req.body.newPassword;

    if (!oldPassword || !newPassword || !oldPassword.trim() || !newPassword.trim()) {

      res.status(400).json({
        message: 'Invalid Request !'
      });

    } else {

      password.changePassword(req.params.id, oldPassword, newPassword)

        .then(function(result) {
          res.status(result.status).json({
            message: result.message
          });
        })
        .catch(function(err) {
          res.status(err.status).json({
            message: err.message
          });
        });

    }
  } else {

    res.status(401).json({
      message: 'Invalid Token !'
    });
  }
});

router.post('/users/:id/password', function(req, res) {

  var email = req.params.id;
  var token = req.body.token;
  var newPassword = req.body.password;

  if (!token || !newPassword || !token.trim() || !newPassword.trim()) {

    password.resetPasswordInit(email)
      .then(function(result) {
        res.status(result.status).json({
          message: result.message
        });
      })
      .catch(function(err) {
        res.status(err.status).json({
          message: err.message
        });
      });
  } else {

    password.resetPasswordFinish(email, token, newPassword)

      .then(function(result) {
        res.status(result.status).json({
          message: result.message
        });
      })
      .catch(function(err) {
        res.status(err.status).json({
          message: err.message
        });
      });
  }
});

function checkToken(req) {
  var token = req.headers['x-access-token'];
  if (token) {
    try {
      var decoded = jwt.verify(token, config.secret);
      return decoded.message === req.params.id;
    } catch (err) {
      return false;
    }
  } else {
    return false;
  }
}
module.exports = router;
