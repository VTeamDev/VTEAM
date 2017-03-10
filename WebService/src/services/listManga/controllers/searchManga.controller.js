const mongoose = require('mongoose');
const moment = require('moment');
const listManga = mongoose.model('listManga');

exports.search = function(req, res, next) {
  const name = req.query.name;
  if (typeof name === 'undefined') {
    res.send({ error: "Query doesn't provided."});
  }
  listManga.ensureIndexes({title: "text"})
  listManga.find({ $text: { $search: name } }, {_id: 0}, function(err, doc) {
    res.send({data: doc})
  }).limit(12);
}
