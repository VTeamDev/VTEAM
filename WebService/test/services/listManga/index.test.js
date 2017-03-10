'use strict';

const assert = require('assert');
const app = require('../../../src/app');

describe('listManga service', function() {
  it('registered the listMangas service', () => {
    assert.ok(app.service('listMangas'));
  });
});
