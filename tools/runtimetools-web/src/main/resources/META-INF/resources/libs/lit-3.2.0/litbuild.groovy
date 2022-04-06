def con = System.console();
def answer = con.readLine 'This script must be executed in a build directory and a current Node/NPM version must be available. Continue (y/n)? ';
if (!('y'.equals(answer.toLowerCase())))
	System.exit(0);

clean();
// Remove previous output
def file = new File('lit-element.js');
file.delete();

println 'Installing node packages, this make take a while...';
def npmcmd = 'npm i --save-dev rollup ';
npmcmd += '@web/rollup-plugin-html ';
npmcmd += '@web/rollup-plugin-copy ';
npmcmd += '@rollup/plugin-node-resolve ';
npmcmd += 'rollup-plugin-terser ';
npmcmd += 'rollup-plugin-minify-html-literals ';
npmcmd += 'rollup-plugin-summary ';
npmcmd += 'lit-html ';
npmcmd += 'lit-element';
npmcmd = npmcmd.execute();
npmcmd.waitForOrKill(120000);

// Configure rollup
def rufile = new File('rollup.config.js');
rufile << '''
import resolve from '@rollup/plugin-node-resolve';

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
''';

def rucmd = 'node_modules/.bin/rollup -c'.execute();
rucmd.waitForOrKill(120000);

clean();

def clean() {
	println 'Cleaning up...'
	def file = new File('package.json');
	file.delete();
	file = new File('package-lock.json');
	file.delete();
	file = new File('node_modules');
	file.deleteDir();
	file = new File('rollup.config.js');
	file.delete();
}

