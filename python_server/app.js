// import mongooseConnect from './util/mongoose.util';
// import mongoose from 'mongoose';

var express = require('express');
var path = require('path');
var index = require('./server/routes/index');
var app = express();

app.set('views', path.join(__dirname, 'server/views/pages'));
app.set('view engine', 'ejs');

app.use('/', index);
app.use('/python', index);

module.exports = app;

var server = app.listen(3000, function() {
    console.log('Express server listening on port 3000');
});
