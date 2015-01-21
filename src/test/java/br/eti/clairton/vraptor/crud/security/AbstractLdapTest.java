package br.eti.clairton.vraptor.crud.security;

import org.junit.BeforeClass;

import com.github.trevershick.test.ldap.LdapServerResource;
import com.github.trevershick.test.ldap.annotations.LdapConfiguration;
import com.github.trevershick.test.ldap.annotations.Ldif;

@LdapConfiguration(ldifs = @Ldif("/data.ldif"), port = 19389)
public class AbstractLdapTest {
	private static LdapServerResource server;

	@BeforeClass
	public static void startup() throws Exception {
		if (server == null) {
			server = new LdapServerResource(new AbstractLdapTest()).start();
		}
	}
}
