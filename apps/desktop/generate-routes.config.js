const { TanStackRouterWebpack } = require('@tanstack/router-plugin/webpack')

module.exports = {
  mode: 'development',
  entry: './src/renderer/router.tsx',
  plugins: [
    new TanStackRouterWebpack({
      target: 'react',
      autoCodeSplitting: true,
      routesDirectory: './src/renderer/routes',
      generatedRouteTree: './src/renderer/routeTree.gen.ts',
    }),
  ],
} 