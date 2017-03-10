'use strict';

const assert = require('assert');
const sortChapters = require('../../../../src\services\manga\hooks\sortChapters.js');

describe('manga sortChapters hook', function() {
  it('hook can be used', function() {
    const mockHook = {
      type: 'after',
      app: {},
      params: {},
      result: {},
      data: {}
    };

    sortChapters()(mockHook);

    assert.ok(mockHook.sortChapters);
  });
});
