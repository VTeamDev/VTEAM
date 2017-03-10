'use strict'

/**
 * Module dependencies.
 */
const mongoose = require('mongoose'), Schema = mongoose.Schema;

const mangaSchema = new Schema({
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
  chapters: []
});

mongoose.model('manga', mangaSchema, 'manga');
