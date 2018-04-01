
var express = require('express');
var path = require('path');
var index = require('./server/routes/index');
var app = express();


////DB서버와 연결
var mongoose =  require('mongoose');
var db = mongoose.connection;
var bodyParser = require('body-parser');
db.on('error',console.error);
db.once('open',function(){
  console.log("connected to mongod server");
});
mongoose.connect('mongodb://taehongkim:1234@ds133388.mlab.com:33388/rumypet')
/////
var User = require('./server/models/user.model');
app.set('views', path.join(__dirname, 'server/views/pages'));
app.set('view engine', 'ejs');

app.use('/', index);
app.use('/python', index);

// app.use(bodyParser.json({ limit }));
// app.use(bodyParser.urlencoded({
//   extended: true,
//   limit,
// }));


module.exports = app;

var server = app.listen(3000, function() {
    console.log('Express server listening on port 3000');
});
