// Jadex Docs buildfile: nothing to build as docs are purely static
// But use markdown-lint for quality checking (especially for checking relative links)
// Cf. https://github.com/appmattus/markdown-lint/ 
//
// Run with '..\gradlew markdownlint'
plugins {
  id("com.appmattus.markdown") version "0.6.0"
}

import com.appmattus.markdown.rules.*

markdownlint {

	// Optional - Include files using RegEx for ALL rules, default=".*" (i.e. all files in root project directory)
    //includes = listOf(".*/quickstart.*/.*")
    
	rules {

		// Allow same subsection name in different super sections (e.g. tutorial exercise "Start the Agent" sections)
		// Bad, because anchors in headings are numbered and thus hard to refer to, if needed
		+NoDuplicateHeaderRule(allowDifferentNesting=true) {
		}

		// Currently broken by our figure captions layout
		+NoEmphasisAsHeaderRule {
			active	= false
		}
		
		
		// TODO: fix docs according to these rules?
		+ConsistentUlStyleRule {
			active	= false
		}
		+ConsistentHeaderStyleRule {
			active	= false
		}
		+UlIndentRule {
			active	= false
		}
		+SingleH1Rule {
			active	= false
		}
		+OlPrefixRule {
			active	= false
		}
		+NoInlineHtmlRule {
			active	= false
		}
		+ListIndentRule {
			active	= false
		}
		+UlStartLeftRule {
			active	= false
		}

		// Rules we don't want to check? (What is our markdown coding style?)
		+NoWhitespaceFilenameRule {
			active	= false
		}
		+LineLengthRule {
			active	= false
		}
		+LowerCaseFilenameRule {
			active	= false
		}
		+NoTrailingPunctuationRule {	// e.g. questionmark at the end of a heading
			active	= false
		}
	}
	
	reports {
		html()
	}
	
	threshold	= 0
}
