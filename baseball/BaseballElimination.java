/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class BaseballElimination {

    private final int numOfTeams;
    private final Map<Integer, String> teams;
    private final int[] wins;
    private final int[] loses;
    private final int[] remainingGames;
    private final int[][] remainingGamesPairs;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        this.numOfTeams = in.readInt();
        this.teams = new HashMap<>();
        this.wins = new int[numOfTeams];
        this.loses = new int[numOfTeams];
        this.remainingGames = new int[numOfTeams];
        this.remainingGamesPairs = new int[numOfTeams][numOfTeams];
        readFile(in);

    }

    // parses the teams from file
    private void readFile(In in) {
        for (int i = 0; i < numOfTeams; i++) {
            teams.put(i, in.readString());
            wins[i] = in.readInt();
            loses[i] = in.readInt();
            remainingGames[i] = in.readInt();
            for (int j = 0; j < numOfTeams; j++) {
                remainingGamesPairs[i][j] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return teams.values();
    }

    // number of wins for given team
    public int wins(String team) {
        return wins[getTeam(team)];
    }


    // number of losses for given team
    public int losses(String team) {
        return loses[getTeam(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        return remainingGames[getTeam(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return remainingGamesPairs[getTeam(team1)][getTeam(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return certificateOfElimination(team) != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        validateTeam(team);
        for (int i = 0; i < numOfTeams; i++) {
            if (isTriviallyEliminated(team, i)) {
                List<String> eliminatedTeam = new ArrayList<>();
                eliminatedTeam.add(teams.get(i));
                return eliminatedTeam;
            }
        }
        int games = (numOfTeams) * (numOfTeams - 1) / 2;
        FordFulkerson fordFulkerson = solveMaxFlow(team, games);
        return certificateOfElimination(games, fordFulkerson);
    }

    // calculates certificate of elimination
    private List<String> certificateOfElimination(int games, FordFulkerson fordFulkerson) {
        List<String> eliminatedTeams = new ArrayList<>();
        for (int i = 0; i < numOfTeams; i++) {
            if (fordFulkerson.inCut(i + games)) {
                eliminatedTeams.add(teams.get(i));
            }
        }
        return eliminatedTeams.isEmpty() ? null : eliminatedTeams;
    }

    // checks if team should be trivially eliminated
    private boolean isTriviallyEliminated(String team, int i) {
        return !team.equals(teams.get(i)) && wins(team) + remaining(team) < wins[i];
    }

    // solves max flow
    private FordFulkerson solveMaxFlow(String team, int games) {
        int verticesCount = numOfTeams + games + 2;
        int source = verticesCount - 2;
        int sink = verticesCount - 1;
        FlowNetwork flowNetwork = createFlowNetwork(team, games, source, sink);
        return new FordFulkerson(flowNetwork, source, sink);
    }

    // creates flow network
    private FlowNetwork createFlowNetwork(String team, int games, int source, int sink) {
        FlowNetwork flowNetwork = new FlowNetwork(numOfTeams + games + 2);
        int vertex = 0;
        for (int col = 0; col < numOfTeams; col++) {
            for (int row = col + 1; row < numOfTeams; row++, vertex++) {
                flowNetwork.addEdge(
                        new FlowEdge(source, vertex, remainingGamesPairs[col][row]));
                flowNetwork
                        .addEdge(new FlowEdge(vertex, col + games, Double.POSITIVE_INFINITY));
                flowNetwork
                        .addEdge(new FlowEdge(vertex, row + games, Double.POSITIVE_INFINITY));
            }
            flowNetwork.addEdge(
                    new FlowEdge(col + games, sink, wins(team) + remaining(team) - wins[col]));
        }
        return flowNetwork;
    }

    // return team's id, or throws exception if team doesn't exist
    private int getTeam(String team) {
        validateTeam(team);
        int ind = -1;
        for (int key : teams.keySet()) {
            if (teams.get(key).equals(team)) {
                ind = key;
                break;
            }
        }
        if (ind != -1) return ind;
        else throw new NoSuchElementException("");
    }

    // validates input team
    private void validateTeam(String team) {
        boolean contains = false;
        for (int key : teams.keySet()) {
            if (teams.get(key).equals(team)) {
                contains = true;
                break;
            }
        }
        if (contains) return;
        else throw new IllegalArgumentException();
    }
}
