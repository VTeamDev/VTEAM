'use strict';

// mangaInfo-model.js - A mongoose model
//
// See http://mongoosejs.com/docs/models.html
// for more of what you can do here.

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const mangaInfoSchema = new Schema({
  text: { type: String, required: true },
}, { collection: 'manga_info'});

const mangaInfoModel = mongoose.model('mangaInfo', mangaInfoSchema);

module.exports = mangaInfoModel;
