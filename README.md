# Install

Download and unzip [activator](https://www.playframework.com/documentation/2.3.x/Installing)

Add it to your path

```
git clone https://github.com/scala-games/eleusis.git
cd eleusis
activator
  eclipse with-source=true
  idea
```

# Run locally

```
cd eleusis
activator -jvm-debug 9999 ~run
```


# Deploy on Openshift

Create an application with cartridge http://cartreflect-claytondev.rhcloud.com/reflect?github=tyrcho/openshift-cartridge-play2&commit=play-2.3.0

```
git remote add oo ssh://caffecaffe@host-domain.rhcloud.com/~/git/host.git/
git push oo master --force

```

# Rules of Eleusis

## French
http://regle.jeuxsoc.fr/eleus_rg.pdf

## English
https://docs.google.com/document/d/19_p1W7m9CwI5jpiQt3m_J0rn_j2TOPjD96nJ2eSzKkE/edit?usp=sharing
