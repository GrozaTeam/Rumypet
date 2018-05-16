var express = require('express');
var router = express.Router();
var multer = require('multer');
var fileType = require('file-type');
var fs = require('fs');

var PythonShell = require('python-shell');
var resultPython;
var mongoose = require ('mongoose');
var options = {
  mode: 'text',
  pythonPath: '',
  pythonOptions: ['-u'],
  scriptPath: './python-code',
  args: ['value1', 'value2']
};
router.get('/', function(req,res){
  res.render('start',{
    title: 'RUMYPET',
    user: req.user
  });
});
router.get('/python', function(req, res) {
  options.args[0] = 'inputImage.png';
  options.args[1] = 'dog3';

  PythonShell.run('DogNoseRecognition.py', options, function (err, resultPython) {
    if (err) throw err;
    console.log('Results: %j', resultPython);
    console.log('Errors: %j', err);
  });

  res.send('python code is sent!Check out the log');

});


module.exports = router;
