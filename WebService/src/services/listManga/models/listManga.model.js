'use strict';

const mongoose = require('mongoose'),
  Schema = mongoose.Schema;

const listMangaSchema = new Schema({
  manga_id: {
    type: String,
    required: 'Please fill manga ID',
    trim: true,
  },
  url: {
    type: String,
  },
  thumbnail: String,
  title: {
    type: String,
    required: 'Please fill title',
  },
})

mongoose.model('listManga', listMangaSchema, 'list_manga');
