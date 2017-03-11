'use strict';

// realtime-model.js - A mongoose model
//
// See http://mongoosejs.com/docs/models.html
// for more of what you can do here.

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const realtimeSchema = new Schema({
  manga: { type: Schema.ObjectId, ref: 'listManga', required: true },
  user: { type: Schema.ObjectId, ref: 'user', required: true, unique: true }
});

mongoose.model('realtime', realtimeSchema);

