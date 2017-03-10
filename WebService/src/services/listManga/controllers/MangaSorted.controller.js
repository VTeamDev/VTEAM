const mongoose = require('mongoose');
const moment = require('moment');
const listManga = mongoose.model('listManga');

const select = ['manga_id', 'thumbnail', 'title'];

const skipTop = 20;
const skipLate = 9;

function genSkip(skip, res) {
  listManga.find({}, {_id: 0}).lean(true)
    .select(select)
    .skip(skip)
    .limit(10)
    .then(function(data) {
      res.send({data});
    })
}

exports.latest = function(req, res, next) {
  genSkip(skipLate, res);
}

exports.top = function(req, res, next) {
  genSkip(skipTop, res);
}

exports.recommend = function(req, res, next) {
  genSkip(moment().millisecond()* 2, res);
}

