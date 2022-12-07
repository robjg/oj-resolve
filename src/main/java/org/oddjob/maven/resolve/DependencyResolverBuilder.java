package org.oddjob.maven.resolve;

import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.oddjob.maven.session.ResolverSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DependencyResolverBuilder {

    private static final Logger logger = LoggerFactory.getLogger(DependencyResolver.class);

    private final ResolverSession resolverSession;

    private DependencyResolverBuilder(ResolverSession resolverSession) {
        this.resolverSession = resolverSession;
    }

    public static DependencyResolverBuilder from(ResolverSession resolverSession) {
        return new DependencyResolverBuilder(resolverSession);
    }

    private String classifier;

    private String extension;

    private String scopes;

    private boolean failOnMissingAttachments;

    public DependencyResolverBuilder withClassifier(String classifier) {
        this.classifier = classifier;
        return this;
    }

    public DependencyResolverBuilder withExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public DependencyResolverBuilder withScopes(String scopes) {
        this.scopes = scopes;
        return this;
    }

    public DependencyResolverBuilder withFailOnMissingAttachments(boolean failOnMissingAttachments) {
        this.failOnMissingAttachments = failOnMissingAttachments;
        return this;
    }

    public DependencyResolver build() {
        return new Impl(this);
    }

    static class Impl implements DependencyResolver {

        private final ResolverSession resolverSession;

        private final String classifier;

        private final String extension;

        private final boolean failOnMissingAttachments;

        private final DependencyFilter filter;

        Impl(DependencyResolverBuilder builder) {
            this.resolverSession = Objects.requireNonNull(builder.resolverSession);
            this.classifier = builder.classifier;
            this.extension = Optional.ofNullable(builder.extension).orElse("jar");
            this.failOnMissingAttachments = builder.failOnMissingAttachments;
            this.filter = Optional.ofNullable(builder.scopes)
                    .map(ResolverUtils::createScopeFilter)
                    .orElse((node, parents) -> true);
        }

        @Override
        public List<ArtifactResult> resolve(DependencyNode root) {

            Group group = new Group( classifier, filter, extension);

            group.createRequests( root );

            logger.info( "Resolving artifacts" );

            List<ArtifactResult> results;
            try
            {
                results = resolverSession.getSystem()
                        .resolveArtifacts( resolverSession.getSession(), group.getRequests() );
            }
            catch ( ArtifactResolutionException e )
            {
                if ( !group.isAttachments() || failOnMissingAttachments )
                {
                    throw new ResolveException( "Could not resolve artifacts: " + e.getMessage(), e );
                }
                results = e.getResults();
                for ( ArtifactResult result : results )
                {
                    if ( result.isMissing() )
                    {
                        logger.debug( "Ignoring missing attachment " + result.getRequest().getArtifact());
                    }
                    else if ( !result.isResolved() )
                    {
                        throw new ResolveException( "Could not resolve artifacts: " + e.getMessage(), e );
                    }
                }
            }

            return results;
        }
    }



    private static class Group
    {
        private final String classifier;

        private final DependencyFilter filter;

        private final String extension;

        private final List<ArtifactRequest> requests = new ArrayList<>();

        Group( String classifier, DependencyFilter filter, String extension )
        {
            this.classifier = classifier;
            this.filter = filter;
            this.extension = extension;
        }

         boolean isAttachments()
        {
            return classifier != null;
        }

        public void createRequests( DependencyNode node )
        {
            createRequests( node, new LinkedList<>() );
        }

        private void createRequests( DependencyNode node, LinkedList<DependencyNode> parents )
        {
            if ( node.getDependency() != null )
            {
                    if ( filter.accept( node, parents ) )
                    {
                        ArtifactRequest request = new ArtifactRequest( node );
                        if ( classifier != null )
                        {
                            request.setArtifact( new SubArtifact( request.getArtifact(), classifier, extension) );
                        }
                        requests.add( request );
                }
            }

            parents.addFirst( node );

            for ( DependencyNode child : node.getChildren() )
            {
                createRequests( child, parents );
            }

            parents.removeFirst();
        }

        public List<ArtifactRequest> getRequests()
        {
            return requests;
        }
    }
}
