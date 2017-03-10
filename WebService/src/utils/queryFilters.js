const omit = require('lodash.omit');
const PROPERTIES = ['$sort', '$limit', '$skip', '$select', '$polulate']

exports.create = function(query) {
  var filters = {
    $sort: query.$sort,
    $select: query.$select,
    $polulate: query.$polulate
  }

  if (typeof query.$skip !== 'undefined') {
    filters.$skip = parseInt(query.$skip);
  }
  filters.$limit = 5;

  if (typeof query.$limit !== 'undefined') {
    filters.$limit = parseInt(query.$limit);
  }

  var results = { filters: filters, query: omit(query, ...PROPERTIES)};

  return results;
}
