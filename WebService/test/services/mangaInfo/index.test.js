'use strict';

const assert = require('assert');
const app = require('../../../src/app');

describe('mangaInfo service', function() {
  it('registered the mangaInfos service', () => {
    assert.ok(app.service('mangaInfos'));
  });
});
