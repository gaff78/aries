/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.aries.subsystem.itests;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.MavenConfiguredJUnit4TestRunner;
import org.osgi.framework.Constants;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;

@RunWith(MavenConfiguredJUnit4TestRunner.class)
public class OptionalDependenciesTest extends SubsystemTest {
	/*
	 * Subsystem-SymbolicName: application.a.esa
	 * Subsystem-Content: bundle.a.jar
	 */
	private static final String APPLICATION_A = "application.a.esa";
	/*
	 * Bundle-SymbolicName: bundle.a.jar
	 * Import-Package: x;resolution:=optional
	 * Require-Bundle: x;resolution:=optional
	 * Require-Capability: x;resolution:=optional
	 */
	private static final String BUNDLE_A = "bundle.a.jar";
	
	private static void createApplicationA() throws IOException {
		createApplicationAManifest();
		createSubsystem(APPLICATION_A, BUNDLE_A);
	}
	
	private static void createApplicationAManifest() throws IOException {
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, APPLICATION_A);
		attributes.put(SubsystemConstants.SUBSYSTEM_CONTENT, BUNDLE_A);
		createManifest(APPLICATION_A + ".mf", attributes);
	}
	
	private static void createBundleA() throws IOException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Constants.IMPORT_PACKAGE, "x;resolution:=optional");
		headers.put(Constants.REQUIRE_BUNDLE, "x;resolution:=optional");
		headers.put(Constants.REQUIRE_CAPABILITY, "x;resolution:=optional");
		createBundle(BUNDLE_A, headers);
	}
	
	private static boolean createdTestFiles;
	@Before
	public static void createTestFiles() throws Exception {
		if (createdTestFiles)
			return;
		createBundleA();
		createApplicationA();
		createdTestFiles = true;
	}
	
	@Test
	public void testOptionalImportPackage() throws Exception {
		try {
			Subsystem subsystem = installSubsystemFromFile(APPLICATION_A);
			try {
				try {
					startSubsystem(subsystem);
				}
				catch (Exception e) {
					e.printStackTrace();
					fail("Missing optional requirements must not cause subsystem start failure");
				}
			}
			finally {
				stopAndUninstallSubsystemSilently(subsystem);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Missing optional requirements must not cause subsystem installation failure");
		}
	}
}
