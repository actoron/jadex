

import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.gradle.internal.impldep.org.apache.maven.artifact.versioning.ComparableVersion;
import org.junit.Test;

public class TestMavenVersionCompare
{
	@Test
	public void	testVersionOrdering()
	{
		Map<String, ComparableVersion>	versions	= new LinkedHashMap<>();
		
		// Release means matching release tag on HEAD.
		// Nightly means clean git state, but no release tag at HEAD.
		// Snapshot means dirty work dir.
		
		// How Maven interprets these versions: earlier means newer
		
//		versions.put("nightly", new ComparableVersion("1.2.3-20181213135700"));
//		versions.put("nightly-branch", new ComparableVersion("1.2.3-mybranch-20181213135700"));		
//		versions.put("release-branch", new ComparableVersion("1.2.3-mybranch"));
//		versions.put("snapshot-branch", new ComparableVersion("1.2.3-mybranch-SNAPSHOT"));
//		versions.put("release", new ComparableVersion("1.2.3"));
//		versions.put("snapshot", new ComparableVersion("1.2.3-SNAPSHOT"));

		versions.put("release-newer", new ComparableVersion("1.2.4"));
		versions.put("lts", new ComparableVersion("1.2.3-sp"));
		versions.put("release", new ComparableVersion("1.2.3"));
		versions.put("snapshot", new ComparableVersion("1.2.3-SNAPSHOT"));
		versions.put("nightly1", new ComparableVersion("1.2.3-beta-20181213135700"));
		versions.put("nightly2", new ComparableVersion("1.2.3-beta-20181212135700"));
		versions.put("nightly1-branch", new ComparableVersion("1.2.3-beta-mybranch-20181213135700"));
		versions.put("nightly2-branch", new ComparableVersion("1.2.3-beta-mybranch-20181212135700"));
		versions.put("release-branch", new ComparableVersion("1.2.3-beta-mybranch"));
		versions.put("snapshot-branch", new ComparableVersion("1.2.3-beta-mybranch-SNAPSHOT"));
		versions.put("beta", new ComparableVersion("1.2.3-beta"));
		versions.put("release-older", new ComparableVersion("1.2.2"));

//		versions.put("nightly-branch", new ComparableVersion("1.2.3.20181213135700-mybranch"));		
//		versions.put("nightly", new ComparableVersion("1.2.3.20181213135700"));
//		versions.put("release-branch", new ComparableVersion("1.2.3-mybranch"));
//		versions.put("snapshot-branch", new ComparableVersion("1.2.3-mybranch-SNAPSHOT"));
//		versions.put("release", new ComparableVersion("1.2.3"));
//		versions.put("snapshot", new ComparableVersion("1.2.3-SNAPSHOT"));

		
		String	previous	= null;
		for(String version: versions.keySet())
		{
			if(previous!=null)
			{
				int	val	= versions.get(previous).compareTo(versions.get(version));
				assertTrue(previous+">"+version+" = "+val, val>0);
			}
			previous	= version;
		}
	}
}
