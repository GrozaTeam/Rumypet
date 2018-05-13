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

//----
var upload = multer({
  dest: './public/images/',
  limits: {
    fileSize: 10000000,
    files: 1
  },
  fileFilter: function(req, file, callback) {
    if (!file.originalname.match(/\.(jpg|jpeg|png)$/)) {
      return callback(new Error('Only Images are allowed !'), false);
    }

    callback(null, true);
  }
}).single('image');

router.post('/images/upload', function(req, res) {
  console.log('uploading is listening');
  upload(req, res, function(err) {
    if (err) {
      console.log(err);
      res.status(400).json({
        message: err.message
      });
    } else {
      var path = 'images/' + req.file.filename;
      res.status(200).json({
        message: 'Image Uploaded Successfully !',
        path: path
      });
    }
  });
});

router.get('/images/:imagename', function(req, res) {
  var imagename = req.params.imagename;
  console.log('imagename=' + imagename);
  // var imagepath = __dirname + "/images/" + imagename;
  var imagepath = "./public/images/" + imagename;
  var image = fs.readFileSync(imagepath);
  var mime = fileType(image).mime;
  res.writeHead(200, {
    'Content-Type': mime
  });
  res.end(image, 'binary');
});

//----

module.exports = router;
