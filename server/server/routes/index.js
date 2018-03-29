var express = require('express');
var router = express.Router();
var PythonShell = require('python-shell');

var options = {

  mode: 'text',
  pythonPath: './Users/paeng/Library/Python/3.6/lib/python',
  scriptPath: '',
  args: ['value1', 'value2', 'value3']

};
router.get('/', function(req,res){
  res.render('start',{
    title: 'global cau',
    user: req.user
  });

  console.log('hi');
});

router.get('/python', function(req, res) {
  PythonShell.run('test.py', options, function (err, results) {




    console.log('results: %j', results);

  });

});

module.exports = router;
