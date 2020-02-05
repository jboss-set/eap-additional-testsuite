package org.server.test.infinispan.distributable.log;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.jboss.dmr.ModelNode;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.junit.Assert;

/**
 * Log checker based on accessing messages through {@link org.jboss.dmr.ModelNode} returned from {@code :read-log-file}
 * operation.
 */
public final class ModelNodeLogChecker implements LogChecker {

    private static final String READ_LOG_FILE_OPERATION = "read-log-file";

    private final ManagementClient client;
    private final int countOfLines;
    private final boolean readLogFromEnd;

    /**
     * Create an instance of log checker. File will be read from the end (tail).
     * 
     * @param client client which will be used to invoke log file reading operation on server
     * @param countOfLines number of lines which will be read. -1 means all lines will be read. Be careful, that log
     *        files can get pretty big and reading whole file may lead to serious memory issues.
     */
    public ModelNodeLogChecker(final ManagementClient client, final int countOfLines) {
        this(client, countOfLines, true);
    }

    /**
     * Create an instance of log checker
     * 
     * @param client client which will be used to invoke log file reading operation on server
     * @param countOfLines number of lines which will be read. -1 means all lines will be read. Be careful, that log
     *        files can get pretty big and reading whole file may lead to serious memory issues.
     * @param readFromEnd true if log file should be read in tail mode
     */
    public ModelNodeLogChecker(final ManagementClient client, final int countOfLines, final boolean readFromEnd) {
        this.client = client;
        this.countOfLines = countOfLines;
        this.readLogFromEnd = readFromEnd;
    }

    @Override
    public boolean logMatches(Pattern pattern) {
        try {
            return anyLineMatchesPredicate(readLogFileFromManagementModel(),
                    (final String line) -> pattern.matcher(line).matches());

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean logContains(String subString) {
        try {
            return anyLineMatchesPredicate(readLogFileFromManagementModel(),
                    (final String line) -> line.contains(subString));

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private ModelNode readLogFileFromManagementModel() throws IOException {

        final ModelNode op = new ModelNode();

        op.get(ModelDescriptionConstants.OP).set(READ_LOG_FILE_OPERATION);
        op.get(ModelDescriptionConstants.OP_ADDR).add("subsystem", "logging");
        op.get("name").set("server.log");
        op.get("tail").set(this.readLogFromEnd);
        op.get("lines").set(this.countOfLines);

        final ModelNode result = this.client.getControllerClient().execute(op);
        Assert.assertEquals("Management operation failed:\n" + result.toString() + "\n",
                ModelDescriptionConstants.SUCCESS, result.get(ModelDescriptionConstants.OUTCOME).asString());

        return result;
    }

    private boolean anyLineMatchesPredicate(final ModelNode logLines, final Predicate<String> predicate) {
        final Optional<String> optionalMatch = logLines.asList()
                .stream()
                .map(ModelNode::asString)
                .filter(predicate)
                .findFirst();

        return optionalMatch.isPresent();
    }

}
