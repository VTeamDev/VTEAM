'use strict';

// manga-model.js - A mongoose model
//
// See http://mongoosejs.com/docs/models.html
// for more of what you can do here.

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const mangaSchema = new Schema({
  text: { type: String, required: true },
}, {collection: 'manga'} );

const mangaModel = mongoose.model('manga', mangaSchema);

module.exports = mangaModel;
