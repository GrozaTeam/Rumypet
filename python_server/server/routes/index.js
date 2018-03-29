var express = require('express');
var router = express.Router();
var PythonShell = require('python-shell');

var options = {

  mode: 'text',
  pythonPath: './python-code',
  pythonOptions: ['-u'],
  scriptPath: '',
  args: ['value1', 'value2', 'value3']

};
router.get('/', function(req,res){
  res.render('start',{
    title: 'global cau',
    user: req.user
  });
});

router.get('/python', function(req, res) {
  PythonShell.run('test.py', options, function (err, results) {

    if (err) throw err;


    console.log('results: %j', results);

  });

});

module.exports = router;
