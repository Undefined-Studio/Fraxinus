const HtmlWebpackPlugin = require('html-webpack-plugin');

config.plugins.push(
    new HtmlWebpackPlugin({
        title: "Fraxinus",
        template: "../resources/main/public/index.html"
    })
);
