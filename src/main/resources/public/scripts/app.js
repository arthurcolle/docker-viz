var graph,
    nodeRadius=15,
    gravity = 0.2,
    distance = 200,
    charge = -450,
    interval = 7;

$("#gravity").val(gravity);
$("#distance").val(distance);
$("#charge").val(charge);

var nodes, links;

function myGraph() {

    // Add and remove elements on the graph object
    this.addNode = function (n) {
        nodes.push(n);
        update();
    };

    this.removeNode = function (id) {
        var i = 0;
        var n = findNode(id);
        while (i < links.length) {
            if ((links[i]['source'] == n) || (links[i]['target'] == n)) {
                links.splice(i, 1);
            }
            else i++;
        }
        nodes.splice(findNodeIndex(id), 1);
        update();
    };

    this.removeLink = function (source, target) {
        for (var i = 0; i < links.length; i++) {
            if (links[i].source.id == source && links[i].target.id == target) {
                links.splice(i, 1);
                break;
            }
        }
        update();
    };

    this.updateCounts = function() {
        $("#numNodes").text(nodes.length);
    };

    this.removeallLinks = function () {
        links.splice(0, links.length);
        update();
    };

    this.removeAllNodes = function () {
        nodes.splice(0, nodes.length);
        update();
    };

    this.addLink = function (lnk) {
        links.push({"source": findNode(lnk.source), "target": findNode(lnk.target), "value": lnk.value});
        update();
    };

    var findNode = function (id) {
        for (var i in nodes) {
            if (nodes[i]["name"] === id) return nodes[i];
        }
    };

    var findNodeIndex = function (id) {
        for (var i = 0; i < nodes.length; i++) {
            if (nodes[i].name == id) {
                return i;
            }
        }
    };

    // set up the D3 visualisation in the specified element
    var w = 600,
        h = 400;

    var vis = d3.select("div#graph")
        .append("svg:svg")
        .attr("width", w)
        .attr("height", h)
        .attr("id", "svg")
        .attr("pointer-events", "all")
        .attr("viewBox", "0 0 " + w + " " + h)
        .attr("perserveAspectRatio", "xMinYMid")
        .append('svg:g');

    var force = d3.layout.force();

    nodes = force.nodes();
    links = force.links();

    var update = function () {
        var link = vis.selectAll("line")
            .data(links, function (d) {
                return d.source.name + "-" + d.target.name;
            });

        link.enter().append("line")
            .attr("id", function (d) {
                return d.source.name + "-" + d.target.name;
            })
            .attr("stroke-width", function (d) {
                return d.value / 10;
            })
            .attr("class", "link");
        link.append("title")
            .text(function (d) {
                return d.value;
            });
        link.exit().remove();

        var node = vis.selectAll("g.node")
            .data(nodes, function (d) {
                return d.name;
            });

        var nodeEnter = node.enter().append("g")
            .attr("class", "node")
            .call(force.drag);

        nodeEnter.append("svg:circle")
            .attr("r", nodeRadius)
            .attr("id", function (d) {
                return "Node;" + d.name;
            })
            .attr("class", "nodeStrokeClass")
            .attr("fill", "#54B6CC");

        /*nodeEnter.append("image")
         .attr("xlink:href", function (d) {
         return d.image
         })
         .attr("x", -8)
         .attr("y", -8)
         .attr("class", "nodeImage")
         .attr("width", 32)
         .attr("height", 32);*/

        // double-click node to inspect
        nodeEnter.on("dblclick", function (d) {
            var containerId = d.container.id;
            d3.xhr('/containers/' + containerId, "text/html", function (err, m) {
                if (err) return console.warn(err);
                d3.select("div#info").html(m.response);
                d3.select("#logs").attr("src", "about:blank");
            });
        });

        nodeEnter.append("svg:text")
            .attr("class", "textClass")
            .attr("x", 21)
            .attr("y", ".32em")
            .text(function (d) {
                return d.name;
            });

        node.exit().remove();

        force.on("tick", function () {

            node.attr("transform", function (d) {
                return "translate(" + d.x + "," + d.y + ")";
            });

            node.attr("cx", function(d) { return d.x = Math.max(nodeRadius, Math.min(w - nodeRadius, d.x)); })
                .attr("cy", function(d) { return d.y = Math.max(nodeRadius, Math.min(h - nodeRadius, d.y)); });

            link.attr("x1", function (d) {
                return d.source.x;
            })
                .attr("y1", function (d) {
                    return d.source.y;
                })
                .attr("x2", function (d) {
                    return d.target.x;
                })
                .attr("y2", function (d) {
                    return d.target.y;
                });
        });

        // Restart the force layout.
        force
            .gravity($("#gravity").val())
            .distance($("#distance").val())
            .charge($("#charge").val())
            .size([w, h])
            .start();
    };


    // Make it all go
    update();
}

function initGraph() {
    graph = new myGraph("#svgdiv");
    clearInfo();
    clearLogs();
}

function updateGraph() {
    d3.json('/cluster', function (c) {
        if (!c) {
            return;
        }
        var nodesToAdd = c.nodes;
        var linksToAdd = c.links;
        var doUpdate = nodesToAdd.length != nodes.length || linksToAdd.length != links.length;

        if (doUpdate) {
            graph.removeAllNodes();
            graph.removeallLinks();

            for (var i = 0; i < nodesToAdd.length; i++) {
                graph.addNode(nodesToAdd[i]);
            }
            for (var j = 0; j < linksToAdd.length; j++) {
                graph.addLink(linksToAdd[j]);
            }
            graph.updateCounts();
            keepNodesOnTop();
        } else {
            console.log("no changes detected; skipping update");
        }
    });
}

var paused = false;
initGraph();
updateGraph();
setInterval(function() {
    if (!paused) {
        updateGraph();
    }
}, interval * 1000);

// because of the way the network is created, nodes are created first, and links second,
// so the lines were on top of the nodes, this just reorders the DOM to put the svg:g on top
function keepNodesOnTop() {
    $(".nodeStrokeClass").each(function( index ) {
        var gnode = this.parentNode;
        gnode.parentNode.appendChild(gnode);
    });
}

function deleteContainer(id) {
    paused = true;
    d3.json('/containers/' + id + '/delete', function (c) {
        updateGraph();
        clearLogs();
        clearInfo();
    });
    paused = false;
}

function clearLogs() {
    $("#logs").attr("src", "about:blank");
}

function clearInfo() {
    $("#info").html('');
}