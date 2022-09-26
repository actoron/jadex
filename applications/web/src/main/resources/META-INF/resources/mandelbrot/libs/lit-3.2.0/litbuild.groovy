def con = System.console();
def answer = con.readLine 'This script must be executed in a build directory and a current Node/NPM version must be available. Continue (y/n)? ';
if (!('y'.equals(answer.toLowerCase())))
	System.exit(0);

clean();
// Remove previous output
def file = new File('lit-element.js');
file.delete();

def sep = File.separator;

println 'Installing node packages, this make take a while...';
def npmcmd = 'npm';
if (System.properties['os.name'].toLowerCase().contains('windows'))
	npmcmd +='.cmd';
npmcmd += ' i --save-dev rollup ';
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
  input: 'node_modules' + sep +'lit-element' + sep + 'lit-element.js',
  output: {
    file: 'lit-element.js',
    format: 'es'
  },
  plugins: [
    resolve({})
  ]
};
''';

def rucmd = 'node_modules'+sep+'.bin'+sep+'rollup';
if (System.properties['os.name'].toLowerCase().contains('windows'))
	rucmd +='.cmd';
rucmd += ' -c';
rucmd = rucmd.execute();
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

