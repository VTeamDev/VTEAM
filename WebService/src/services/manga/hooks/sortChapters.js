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
    var x = moment(a.time, "DD/MM/YYYY H:m");
    var y = moment(b.time, "DD/MM/YYYY H:m");
    // console.log(x);
    var e = x-y;
    if (e == 0) {
      var nameA = a.name;
      var nameB = b.name;
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
    const newChapters = chapters.map(function(item){
      var name = _.keys(item)[0]
      item = item[_.keys(item)[0]];
      item.name = name;
      return item;
    })
    hook.result.chapters = newChapters;
    _.assign(hook.result.chapters, sortChap(newChapters));
    //console.log(sortChap(chapters));
    hook.sortChapters = true;
  };
};
