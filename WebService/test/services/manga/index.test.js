'use strict';

const assert = require('assert');
const app = require('../../../src/app');

describe('manga service', function() {
  it('registered the mangas service', () => {
    assert.ok(app.service('mangas'));
  });
});
