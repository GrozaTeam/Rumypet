var express = require('express');
var router = express.Router();
var PythonShell = require('python-shell');
var resultPython;
var mongoose = require ('mongoose');
var options = {
  mode: 'text',
  pythonPath: '',
  pythonOptions: ['-u'],
  scriptPath: './python-code',
  args: ['value1', 'value2', 'value3']
};
router.get('/', function(req,res){
  res.render('start',{
    title: 'global cau',
    user: req.user
  });
});
router.get('/python', function(req, res) {
  PythonShell.run('test.py', options, function (err, resultPython) {

    if (err) throw err;
    console.log('Results: %j', resultPython);
    console.log('Errors: %j', err);
  });

  res.send('python code is sent!Check out the log');

});

module.exports = router;
