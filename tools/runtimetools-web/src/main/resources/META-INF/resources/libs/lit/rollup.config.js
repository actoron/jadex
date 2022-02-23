import resolve from 'rollup-plugin-node-resolve';

export default {
  input: 'node_modules/lit-element/lit-element.js',
  output: {
    file: 'lit-element.js',
    format: 'es'
  },
  plugins: [
    resolve({})
  ]
};

