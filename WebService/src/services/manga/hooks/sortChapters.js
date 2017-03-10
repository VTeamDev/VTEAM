'use strict';

// src\services\manga\hooks\sortChapters.js
//
// Use this hook to manipulate incoming or outgoing data.
// For more information on hooks see: http://docs.feathersjs.com/hooks/readme.html

const defaults = {};
const moment = require('moment');
const _ = require('lodash');

function sortChap(chapters) {
  const newChapters = chapters.sort(function(a, b) {
    var x = moment(a[_.keys(a)[0]].time, "DD/MM/YYYY H:m");
    var y = moment(b[_.keys(b)[0]].time, "DD/MM/YYYY H:m");
    var e = x-y;
    if (e == 0) {
      var nameA = _.keys(a)[0];
      var nameB = _.keys(b)[0];
      if (nameA == nameB) {
        return 0;
      } else if (nameA < nameB) {
        return -1;
      } else return 1;
    }
    return e;
  });
  return newChapters;
}

module.exports = function(options) {
  options = Object.assign({}, defaults, options);
  return function(hook) {
    const chapters = hook.result.chapters;
    _.assign(hook.result.chapters, sortChap(chapters));
    console.log(sortChap(chapters));
    hook.sortChapters = true;
  };
};
