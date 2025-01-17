import { expect } from 'chai'
import {
    AST_NODE_TYPES,
    TSESTree,
    parse }
    from "@typescript-eslint/typescript-estree"
import ClassBody = TSESTree.ClassBody
import ClassElement = TSESTree.ClassElement
import Identifier = TSESTree.Identifier
import Literal = TSESTree.Literal
import ProgramStatement = TSESTree.ProgramStatement
import TSModuleBlock = TSESTree.TSModuleBlock
import TSModuleDeclaration = TSESTree.TSModuleDeclaration
import TSInterfaceBody = TSESTree.TSInterfaceBody
import TypeElement = TSESTree.TypeElement
import * as fs from 'fs'


export default class VerifyModule
{
    private moduleName: string
    private instances: Map<string, any>

    constructor( moduleName: string, instances: Array<any> )
    {
        this.moduleName = moduleName
        this.instances = new Map( instances.map( i => {
            if ( i instanceof Array ) return [ i[0], i[1] ] // Type name specified manually.
            else return [ i.constructor.name, i ] // Type name inferred from constructor.
        } ) )
    }

    async verify(): Promise<void>
    {
        const declarationFile = `./@types/${this.moduleName}/index.d.ts`
        const source = fs.readFileSync( declarationFile ).toString()
        const ast = parse( source )
    
        const scope = await Promise.resolve( import( this.moduleName ) )
        for ( const statement of ast.body )
        {
            this.verifyStatement( statement, scope )
        }
    }
    
    verifyStatement( statement: ProgramStatement, scope: any ): void
    {
        switch ( statement.type )
        {
            case AST_NODE_TYPES.TSModuleDeclaration:
            case AST_NODE_TYPES.ClassDeclaration:
            {
                const newScope = this.verifyIdentifier( statement.id, scope )
                if ( statement.body != undefined )
                {
                    this.verifyBody( statement.body, newScope )
                }
                break;
            }
            case AST_NODE_TYPES.TSDeclareFunction:
            {
                this.verifyIdentifier( statement.id, scope )
                break;
            }
            case AST_NODE_TYPES.TSInterfaceDeclaration:
            {
                const interfaceName = statement.id.name
                const instance = this.getInstance( interfaceName )
                this.verifyBody( statement.body, instance )
                break;
            }
            case AST_NODE_TYPES.VariableDeclaration:
            {   
                const declarations = statement.declarations
                if ( declarations.length != 1 )
                {
                    throw( Error( `Only one declarator expected. Support for none or more not implemented.` ) )
                }
                const declaratorId = declarations[ 0 ].id as Identifier
                this.verifyIdentifier( declaratorId, scope )
                break;
            }
            case AST_NODE_TYPES.ImportDeclaration:
            case AST_NODE_TYPES.TSImportEqualsDeclaration:
                // Skip.
                break;
            default:
                throw( Error( `verifyStatement: Verifying valid declaration of '${statement.type}' is not implemented.` ) )
        }
    }
    
    verifyBody( item: TSModuleDeclaration | TSModuleBlock | ClassBody | TSInterfaceBody, scope: any ): void
    {
        switch ( item.type )
        {
            case AST_NODE_TYPES.TSModuleDeclaration:
            {
                const newScope = this.verifyIdentifier( item.id, scope )
                if ( item.body != undefined )
                {
                    this.verifyBody( item.body, newScope )
                }
                break;
            }
            case AST_NODE_TYPES.TSModuleBlock:
                for ( const statement of item.body )
                {
                    this.verifyStatement( statement, scope )
                }
                break;
            case AST_NODE_TYPES.ClassBody:
                for ( const classElement of item.body )
                {
                    this.verifyClassElement( classElement, scope )
                }
                break;
            case AST_NODE_TYPES.TSInterfaceBody:
                for ( const interfaceElement of item.body )
                {
                    this.verifyTypeElement( interfaceElement, scope )
                }
                break;
        }
    }
    
    verifyClassElement( element: ClassElement, scope: any ): void
    {
        switch ( element.type )
        {
            case AST_NODE_TYPES.MethodDefinition:
                if ( element.key.type == AST_NODE_TYPES.Identifier )
                {
                    const identifier = element.key as Identifier
                    const scopeToCheck = element.static
                        ? scope
                        : element.kind == 'get'
                            ? this.getInstance( scope.name )
                            : scope.prototype
                    this.verifyIdentifier( identifier, scopeToCheck )
                }
                else
                {
                    throw( Error( `verifyClassElement: Verifying valid declaration of '${element.key.type}' is not implemented.` ) )
                }
                break;
            case AST_NODE_TYPES.PropertyDefinition:
            {
                const scopeToCheck = element.static
                    ? scope
                    : this.getInstance( scope.name )
                this.verifyIdentifier( element.key as Identifier, scopeToCheck )
                break;
            }
            default:
                throw( Error( `verifyClassElement: Verifying valid declaration of '${element.type}' is not implemented.` ) )
        }
    }

    verifyTypeElement( element: TypeElement, scope: any ): void
    {
        switch ( element.type )
        {
            case AST_NODE_TYPES.TSMethodSignature:
            case AST_NODE_TYPES.TSPropertySignature:
                this.verifyIdentifier( element.key as Identifier, scope )
                break;
            default:
                throw( Error( `verifyTypeElement: Verifying valid declaration of '${element.type}' is not implemented.` ) )    
        }
    }
    
    verifyIdentifier( id: Identifier | Literal | null, scope: any ): any
    {
        if ( id?.type != AST_NODE_TYPES.Identifier ) return scope
    
        const identifier = id.name
        const resolved = scope[ identifier ]
        expect( resolved, identifier ).not.undefined
    
        return resolved
    }
    
    getInstance( className: string ): any
    {
        if ( !this.instances.has( className ) )
        {
            throw( Error( `No instance of '${className}' provided for testing.` ) )
        }

        return this.instances.get( className )
    }    
}
