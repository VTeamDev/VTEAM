'use strict'

/**
 * Module dependencies.
 */
const mongoose = require('mongoose'), Schema = mongoose.Schema;

const mangaInfoSchema = new Schema({
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
  category: [String],
  description: String,
  image: String,
  content: String,
});

mongoose.model('mangaInfo', mangaInfoSchema, 'manga_info');

