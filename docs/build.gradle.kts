// Jadex Docs buildfile: nothing to build as docs are purely static
// But use markdown-lint for quality checking (especially for checking relative links)
// Cf. https://github.com/appmattus/markdown-lint/ 
//
// Run with '..\gradlew markdownlint'
plugins {
  id("com.appmattus.markdown") version "0.6.0"
}

import com.appmattus.markdown.rules.*
import com.appmattus.markdown.rules.config.*

markdownlint {

	// Optional - Include/exclude files using RegEx for ALL rules
	// default includes=".*" (i.e. all files in root project directory)
    // excludes = listOf(".*old.*")
    
	rules {

		// Allow same subsection name in different super sections (e.g. tutorial exercise "Start the Agent" sections)
		// Bad, because anchors in headings are numbered and thus hard to refer to, if needed
		+NoDuplicateHeaderRule(allowDifferentNesting=true) {
		}

		// Currently broken by our figure captions layout
		+NoEmphasisAsHeaderRule {
			active	= false
		}
		
		// Find lists with inconsistent numbering
		+OlPrefixRule(style = OrderedListStyle.Ordered) {
		}
		
		// Which header style do we prefer?
		+ConsistentHeaderStyleRule(style = HeaderStyle.SetextWithAtx) {
			active	= false
		}
		
		// Multi page documents (i.e. tutorials and guides) should only have one H1 heading per page
		// so we can combine them into a large document, if we want to.
		+SingleH1Rule {
//			includes	= listOf(".*tutorials.*", ".*guides.*", ".*tools.*")
			includes	= listOf(".*quickstart.*")
		}

		// Currently used for <x-hint> boxes		
		+NoInlineHtmlRule {
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
		
		// Don't care for UL style (e.g. dash vs. asterisk), but enforce consistency in each .md 
		+ConsistentUlStyleRule(style = UnorderedListStyle.Consistent) {
		}
	}
	
	reports {
		html()
	}
	
	threshold	= 0
}
