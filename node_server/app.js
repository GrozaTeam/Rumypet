var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var logger = require('morgan');
var index = require('./server/routes/index');
var routes = require('./server/routes/routes');
var mongoose = require('mongoose');
var app = express();
var router = express.Router();

app.use(bodyParser.json());
app.use(logger('dev'));

app.set('views', path.join(__dirname, 'server/views/pages'));
app.set('view engine', 'ejs');

mongoose.Promise = global.Promise;
mongoose.connect('mongodb://localhost:27017/rumypet');

/*
require('./server/routes/routes')(router);
app.use('/api/v1',router);
*/

app.use('/', index);
app.use('/api/v1', routes);

app.use(function(err, req, res, next){
    if (err.code == 'ENOENT') {
        res.status(404).json({message: 'Image Not Found !'});
    } else {
        res.status(500).json({message:err.message});
    }
});

module.exports = app;

var server = app.listen(8080, function() {
    console.log('Express server listening on port 8080');
});
